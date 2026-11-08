-- =====================================================================
-- GYEMA — Banco de dados (Supabase / PostgreSQL)
-- Arquivo 2 de 3: segurança (Row Level Security)
--
-- Regra geral: o app usa a chave pública (anon/publishable), então TODA
-- proteção dos dados fica aqui. Sem uma política, a tabela fica fechada.
--   • aluno: vê e altera apenas os próprios dados;
--   • instrutor: vê os alunos e gerencia equipamentos, fichas e relatos;
--   • comunidade e ocupação da academia: visíveis para qualquer usuário logado.
-- =====================================================================

alter table public.perfis                 enable row level security;
alter table public.perguntas_prontidao    enable row level security;
alter table public.respostas_prontidao    enable row level security;
alter table public.setores                enable row level security;
alter table public.exercicios             enable row level security;
alter table public.equipamentos           enable row level security;
alter table public.equipamento_exercicios enable row level security;
alter table public.utilizacoes            enable row level security;
alter table public.relatos_problema       enable row level security;
alter table public.fichas_treino          enable row level security;
alter table public.itens_ficha            enable row level security;
alter table public.execucoes_treino       enable row level security;
alter table public.series_executadas      enable row level security;
alter table public.posts                  enable row level security;
alter table public.curtidas               enable row level security;
alter table public.comentarios            enable row level security;
alter table public.grupos                 enable row level security;
alter table public.membros_grupo          enable row level security;

-- ---------------------------------------------------------------------
-- Perfis
-- ---------------------------------------------------------------------
create policy "perfis: ver o próprio ou instrutor vê todos"
    on public.perfis for select to authenticated
    using (id = auth.uid() or public.eh_instrutor());

create policy "perfis: editar o próprio"
    on public.perfis for update to authenticated
    using (id = auth.uid()) with check (id = auth.uid());

-- O usuário não pode mudar o próprio perfil (aluno → instrutor), e-mail ou id.
revoke update on public.perfis from authenticated, anon;
grant update (nome, matricula, cpf, curso, data_nascimento, genero,
              peso_kg, altura_cm, contato_emergencia)
    on public.perfis to authenticated;

-- ---------------------------------------------------------------------
-- Teste de prontidão (dados de saúde: só o dono e instrutores)
-- ---------------------------------------------------------------------
create policy "perguntas: leitura pública"
    on public.perguntas_prontidao for select to anon, authenticated
    using (true);

create policy "respostas: ver as próprias ou instrutor"
    on public.respostas_prontidao for select to authenticated
    using (usuario_id = auth.uid() or public.eh_instrutor());

create policy "respostas: inserir as próprias"
    on public.respostas_prontidao for insert to authenticated
    with check (usuario_id = auth.uid());

create policy "respostas: atualizar as próprias"
    on public.respostas_prontidao for update to authenticated
    using (usuario_id = auth.uid()) with check (usuario_id = auth.uid());

-- ---------------------------------------------------------------------
-- Academia (setores, exercícios, equipamentos): todos leem, instrutor edita
-- ---------------------------------------------------------------------
create policy "setores: leitura" on public.setores
    for select to authenticated using (true);
create policy "setores: instrutor gerencia" on public.setores
    for all to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

create policy "exercicios: leitura" on public.exercicios
    for select to authenticated using (true);
create policy "exercicios: instrutor gerencia" on public.exercicios
    for all to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

create policy "equipamentos: leitura" on public.equipamentos
    for select to authenticated using (true);
create policy "equipamentos: instrutor gerencia" on public.equipamentos
    for all to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

create policy "equip_exercicios: leitura" on public.equipamento_exercicios
    for select to authenticated using (true);
create policy "equip_exercicios: instrutor gerencia" on public.equipamento_exercicios
    for all to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

-- ---------------------------------------------------------------------
-- Utilização de equipamentos
-- Todos veem (para saber o que está ocupado); cada um inicia/finaliza o seu.
-- ---------------------------------------------------------------------
create policy "utilizacoes: leitura" on public.utilizacoes
    for select to authenticated using (true);

create policy "utilizacoes: iniciar a própria" on public.utilizacoes
    for insert to authenticated with check (usuario_id = auth.uid());

create policy "utilizacoes: finalizar a própria ou instrutor" on public.utilizacoes
    for update to authenticated
    using (usuario_id = auth.uid() or public.eh_instrutor())
    with check (usuario_id = auth.uid() or public.eh_instrutor());

-- ---------------------------------------------------------------------
-- Relatos de problema
-- ---------------------------------------------------------------------
create policy "relatos: ver os próprios ou instrutor" on public.relatos_problema
    for select to authenticated using (usuario_id = auth.uid() or public.eh_instrutor());

create policy "relatos: criar" on public.relatos_problema
    for insert to authenticated with check (usuario_id = auth.uid() and status = 'aberto');

create policy "relatos: instrutor atualiza" on public.relatos_problema
    for update to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

-- ---------------------------------------------------------------------
-- Fichas de treino (instrutor monta, aluno consulta as suas)
-- ---------------------------------------------------------------------
create policy "fichas: aluno vê as suas, instrutor vê todas" on public.fichas_treino
    for select to authenticated using (aluno_id = auth.uid() or public.eh_instrutor());
create policy "fichas: instrutor gerencia" on public.fichas_treino
    for all to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

create policy "itens_ficha: leitura conforme a ficha" on public.itens_ficha
    for select to authenticated using (
        exists (select 1 from public.fichas_treino f
                 where f.id = ficha_id
                   and (f.aluno_id = auth.uid() or public.eh_instrutor())));
create policy "itens_ficha: instrutor gerencia" on public.itens_ficha
    for all to authenticated using (public.eh_instrutor()) with check (public.eh_instrutor());

-- ---------------------------------------------------------------------
-- Execuções de treino e séries (histórico/evolução do aluno)
-- ---------------------------------------------------------------------
create policy "execucoes: ver as próprias ou instrutor" on public.execucoes_treino
    for select to authenticated using (usuario_id = auth.uid() or public.eh_instrutor());
create policy "execucoes: criar as próprias" on public.execucoes_treino
    for insert to authenticated with check (usuario_id = auth.uid());
create policy "execucoes: atualizar as próprias" on public.execucoes_treino
    for update to authenticated using (usuario_id = auth.uid()) with check (usuario_id = auth.uid());
create policy "execucoes: excluir as próprias" on public.execucoes_treino
    for delete to authenticated using (usuario_id = auth.uid());

create policy "series: ver conforme a execução" on public.series_executadas
    for select to authenticated using (
        exists (select 1 from public.execucoes_treino x
                 where x.id = execucao_id
                   and (x.usuario_id = auth.uid() or public.eh_instrutor())));
create policy "series: registrar nas próprias execuções" on public.series_executadas
    for insert to authenticated with check (
        exists (select 1 from public.execucoes_treino x
                 where x.id = execucao_id and x.usuario_id = auth.uid()));
create policy "series: excluir das próprias execuções" on public.series_executadas
    for delete to authenticated using (
        exists (select 1 from public.execucoes_treino x
                 where x.id = execucao_id and x.usuario_id = auth.uid()));

-- ---------------------------------------------------------------------
-- Comunidade
-- ---------------------------------------------------------------------
create policy "posts: leitura" on public.posts
    for select to authenticated using (true);
create policy "posts: publicar" on public.posts
    for insert to authenticated with check (autor_id = auth.uid());
create policy "posts: editar o próprio" on public.posts
    for update to authenticated using (autor_id = auth.uid()) with check (autor_id = auth.uid());
create policy "posts: excluir o próprio ou instrutor" on public.posts
    for delete to authenticated using (autor_id = auth.uid() or public.eh_instrutor());

create policy "curtidas: leitura" on public.curtidas
    for select to authenticated using (true);
create policy "curtidas: curtir" on public.curtidas
    for insert to authenticated with check (usuario_id = auth.uid());
create policy "curtidas: descurtir" on public.curtidas
    for delete to authenticated using (usuario_id = auth.uid());

create policy "comentarios: leitura" on public.comentarios
    for select to authenticated using (true);
create policy "comentarios: comentar" on public.comentarios
    for insert to authenticated with check (autor_id = auth.uid());
create policy "comentarios: excluir o próprio ou instrutor" on public.comentarios
    for delete to authenticated using (autor_id = auth.uid() or public.eh_instrutor());

-- ---------------------------------------------------------------------
-- Grupos
-- ---------------------------------------------------------------------
create policy "grupos: ver públicos ou os que participo" on public.grupos
    for select to authenticated using (
        privacidade = 'publico' or criador_id = auth.uid() or public.eh_membro(id));
create policy "grupos: criar" on public.grupos
    for insert to authenticated with check (criador_id = auth.uid());
create policy "grupos: criador edita" on public.grupos
    for update to authenticated using (criador_id = auth.uid()) with check (criador_id = auth.uid());
create policy "grupos: criador exclui" on public.grupos
    for delete to authenticated using (criador_id = auth.uid());

create policy "membros: ver membros de grupos visíveis" on public.membros_grupo
    for select to authenticated using (
        public.eh_membro(grupo_id)
        or exists (select 1 from public.grupos g
                    where g.id = grupo_id
                      and (g.privacidade = 'publico' or g.criador_id = auth.uid())));
create policy "membros: entrar em grupo público ou criador adiciona" on public.membros_grupo
    for insert to authenticated with check (
        exists (select 1 from public.grupos g
                 where g.id = grupo_id
                   and ((usuario_id = auth.uid() and g.privacidade = 'publico')
                        or g.criador_id = auth.uid())));
create policy "membros: sair ou criador remove" on public.membros_grupo
    for delete to authenticated using (
        usuario_id = auth.uid()
        or exists (select 1 from public.grupos g
                    where g.id = grupo_id and g.criador_id = auth.uid()));

-- Quem cria um grupo entra nele automaticamente
create or replace function public.adicionar_criador_como_membro()
returns trigger
language plpgsql security definer set search_path = public
as $$
begin
    insert into public.membros_grupo (grupo_id, usuario_id)
    values (new.id, new.criador_id)
    on conflict do nothing;
    return new;
end $$;

create trigger grupos_criador_membro
    after insert on public.grupos
    for each row execute function public.adicionar_criador_como_membro();

-- ---------------------------------------------------------------------
-- Views e funções: acesso só para quem está logado
-- (perfis_publicos e feed_posts rodam com permissão do dono para poder
--  mostrar o nome do autor sem liberar a tabela perfis inteira)
-- ---------------------------------------------------------------------
revoke all on public.perfis_publicos, public.feed_posts,
              public.ocupacao_setores, public.equipamentos_detalhados,
              public.evolucao_carga_mensal
    from anon;
grant select on public.perfis_publicos, public.feed_posts,
                public.ocupacao_setores, public.equipamentos_detalhados,
                public.evolucao_carga_mensal
    to authenticated;

revoke execute on function public.encerrar_utilizacoes_expiradas() from anon, public;
grant  execute on function public.encerrar_utilizacoes_expiradas() to authenticated;
