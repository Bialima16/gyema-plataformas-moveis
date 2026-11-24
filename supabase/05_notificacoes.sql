-- =====================================================================
-- GYEMA — Arquivo 5: notificações
--
-- Não existe tabela de notificações: a view "notificacoes" monta os avisos
-- a partir do que o banco já registra. Cada usuário só vê os próprios.
--
--   Aluno:      novo treino criado pelo instrutor
--               relato de problema em análise / resolvido
--               comentário ou curtida em um post seu
--   Instrutor:  relato de problema em aberto
--
-- Lidas / não lidas: o app guarda no aparelho a data em que o usuário abriu
-- as notificações pela última vez e conta as que têm criado_em mais novo.
-- =====================================================================

-- Relatos passam a registrar quando mudaram de status
alter table public.relatos_problema
    add column if not exists atualizado_em timestamptz not null default now();

update public.relatos_problema
   set atualizado_em = coalesce(resolvido_em, criado_em);

drop trigger if exists relatos_atualizado_em on public.relatos_problema;
create trigger relatos_atualizado_em
    before update on public.relatos_problema
    for each row execute function public.tocar_atualizado_em();

create or replace view public.notificacoes
with (security_invoker = true) as
select *
from (
    -- Aluno: treino novo
    select 'ficha-' || f.id                        as id,
           f.aluno_id                              as usuario_id,
           'treino_novo'                           as tipo,
           'Novo treino'                           as titulo,
           'Seu instrutor criou o treino "' || f.nome || '".' as mensagem,
           f.id                                    as referencia_id,
           f.criado_em                             as criado_em
      from public.fichas_treino f
     where f.ativa
       and f.instrutor_id is distinct from f.aluno_id

    union all

    -- Aluno: relato mudou de status
    select 'relato-' || r.id || '-' || r.status,
           r.usuario_id,
           'relato_atualizado',
           case r.status when 'resolvido' then 'Problema resolvido'
                         else 'Relato em análise' end,
           case r.status
               when 'resolvido' then 'O problema que você relatou em ' || e.nome || ' foi resolvido.'
               else 'O problema que você relatou em ' || e.nome || ' está em análise.'
           end,
           r.id,
           r.atualizado_em
      from public.relatos_problema r
      join public.equipamentos e on e.id = r.equipamento_id
     where r.status <> 'aberto'

    union all

    -- Instrutor: relatos em aberto
    select 'relato-aberto-' || r.id,
           auth.uid(),
           'relato_aberto',
           'Novo relato de problema',
           e.nome || ': ' || r.categoria || '.',
           r.id,
           r.criado_em
      from public.relatos_problema r
      join public.equipamentos e on e.id = r.equipamento_id
     where r.status = 'aberto'
       and public.eh_instrutor()

    union all

    -- Autor do post: comentários de outras pessoas
    select 'comentario-' || c.id,
           p.autor_id,
           'comentario',
           'Novo comentário',
           coalesce(a.nome, 'Alguém') || ' comentou no seu post.',
           p.id,
           c.criado_em
      from public.comentarios c
      join public.posts p on p.id = c.post_id
      left join public.perfis_publicos a on a.id = c.autor_id
     where c.autor_id <> p.autor_id

    union all

    -- Autor do post: curtidas de outras pessoas
    select 'curtida-' || k.post_id || '-' || k.usuario_id,
           p.autor_id,
           'curtida',
           'Nova curtida',
           coalesce(a.nome, 'Alguém') || ' curtiu seu post.',
           p.id,
           k.criado_em
      from public.curtidas k
      join public.posts p on p.id = k.post_id
      left join public.perfis_publicos a on a.id = k.usuario_id
     where k.usuario_id <> p.autor_id
) n
where n.usuario_id = auth.uid()
  and n.criado_em > now() - interval '30 days';

revoke all on public.notificacoes from anon;
grant select on public.notificacoes to authenticated;
