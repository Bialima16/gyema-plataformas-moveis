-- =====================================================================
-- GYEMA — Banco de dados (Supabase / PostgreSQL)
-- Arquivo 1 de 3: tabelas, tipos, funções e gatilhos
--
-- Como usar: Supabase → SQL Editor → New query → cole este arquivo → Run.
-- Depois rode 02_seguranca_rls.sql e 03_dados_iniciais.sql, nessa ordem.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tipos (enums)
-- ---------------------------------------------------------------------
create type public.perfil_usuario     as enum ('aluno', 'instrutor');
create type public.status_equipamento as enum ('disponivel', 'ocupado', 'manutencao');
create type public.status_relato      as enum ('aberto', 'em_analise', 'resolvido');
create type public.privacidade_grupo  as enum ('publico', 'privado');
create type public.tipo_exercicio_eq  as enum ('principal', 'alternativo');

-- ---------------------------------------------------------------------
-- Usuários
-- Cada conta do Supabase Auth (auth.users) ganha uma linha em "perfis",
-- criada automaticamente pelo gatilho mais abaixo.
-- ---------------------------------------------------------------------
create table public.perfis (
    id                  uuid primary key references auth.users (id) on delete cascade,
    nome                text not null,
    email               text not null,
    perfil              public.perfil_usuario not null default 'aluno',
    matricula           text unique,
    cpf                 text unique,
    curso               text,
    data_nascimento     date,
    genero              text,
    peso_kg             numeric(5, 2) check (peso_kg   between 20 and 400),
    altura_cm           numeric(5, 1) check (altura_cm between 80 and 260),
    contato_emergencia  text,
    criado_em           timestamptz not null default now(),
    atualizado_em       timestamptz not null default now()
);

-- Teste de prontidão (cadastro, passo 3)
create table public.perguntas_prontidao (
    id     smallint primary key,
    texto  text not null
);

create table public.respostas_prontidao (
    usuario_id    uuid     not null references public.perfis (id) on delete cascade,
    pergunta_id   smallint not null references public.perguntas_prontidao (id),
    resposta      boolean  not null,
    respondido_em timestamptz not null default now(),
    primary key (usuario_id, pergunta_id)
);

-- ---------------------------------------------------------------------
-- Academia: setores (zonas do mapa), equipamentos e exercícios
-- ---------------------------------------------------------------------
create table public.setores (
    id      smallint generated always as identity primary key,
    nome    text not null unique,
    icone   text,                       -- nome do ícone usado no app (opcional)
    em_manutencao boolean not null default false
);

create table public.exercicios (
    id             integer generated always as identity primary key,
    nome           text not null unique,
    grupo_muscular text not null
);

create table public.equipamentos (
    id                     integer generated always as identity primary key,
    codigo                 text not null unique,          -- ex.: SUP-01, EQ-023 (usado no QR Code)
    nome                   text not null,
    setor_id               smallint not null references public.setores (id),
    grupo_muscular         text,
    capacidade_simultanea  smallint not null default 1 check (capacidade_simultanea > 0),
    tempo_maximo_min       smallint not null default 30 check (tempo_maximo_min > 0),
    status                 public.status_equipamento not null default 'disponivel',
    instrucoes_uso         text,
    video_url              text,
    criado_em              timestamptz not null default now()
);

create table public.equipamento_exercicios (
    equipamento_id integer not null references public.equipamentos (id) on delete cascade,
    exercicio_id   integer not null references public.exercicios (id) on delete cascade,
    tipo           public.tipo_exercicio_eq not null default 'principal',
    primary key (equipamento_id, exercicio_id)
);

-- Uso de um equipamento por um aluno (QR Code → "Em utilização" → "Finalizar")
create table public.utilizacoes (
    id              bigint generated always as identity primary key,
    equipamento_id  integer not null references public.equipamentos (id) on delete cascade,
    usuario_id      uuid    not null references public.perfis (id) on delete cascade,
    inicio          timestamptz not null default now(),
    fim_previsto    timestamptz,
    fim             timestamptz,
    check (fim is null or fim >= inicio)
);

-- Um aluno só pode estar usando um equipamento por vez
create unique index utilizacoes_uma_ativa_por_usuario
    on public.utilizacoes (usuario_id) where fim is null;
create index utilizacoes_ativas_por_equipamento
    on public.utilizacoes (equipamento_id) where fim is null;

-- Relatos de problemas em equipamentos
create table public.relatos_problema (
    id              bigint generated always as identity primary key,
    equipamento_id  integer not null references public.equipamentos (id) on delete cascade,
    usuario_id      uuid    not null references public.perfis (id) on delete cascade,
    categoria       text    not null default 'Outros',
    descricao       text    not null check (length(trim(descricao)) > 0),
    status          public.status_relato not null default 'aberto',
    criado_em       timestamptz not null default now(),
    resolvido_em    timestamptz
);

-- ---------------------------------------------------------------------
-- Treinos: fichas montadas pelo instrutor e execuções do aluno
-- ---------------------------------------------------------------------
create table public.fichas_treino (
    id            bigint generated always as identity primary key,
    aluno_id      uuid not null references public.perfis (id) on delete cascade,
    instrutor_id  uuid references public.perfis (id) on delete set null,
    nome          text not null,                  -- ex.: "Treino A — Peito e Tríceps"
    grupo_foco    text,                           -- ex.: "Peito e Tríceps"
    ordem         smallint not null default 1,    -- A=1, B=2, ...
    ativa         boolean not null default true,
    criado_em     timestamptz not null default now()
);

create table public.itens_ficha (
    id             bigint generated always as identity primary key,
    ficha_id       bigint  not null references public.fichas_treino (id) on delete cascade,
    exercicio_id   integer not null references public.exercicios (id),
    ordem          smallint not null default 1,
    series         smallint not null default 3  check (series > 0),
    repeticoes     smallint not null default 10 check (repeticoes > 0),
    carga_kg       numeric(6, 2),
    descanso_seg   smallint,
    observacoes    text,
    tecnica        text                         -- ex.: "Drop-set na última série"
);

create table public.execucoes_treino (
    id          bigint generated always as identity primary key,
    usuario_id  uuid   not null references public.perfis (id) on delete cascade,
    ficha_id    bigint references public.fichas_treino (id) on delete set null,
    inicio      timestamptz not null default now(),
    fim         timestamptz,
    check (fim is null or fim >= inicio)
);

create table public.series_executadas (
    id             bigint generated always as identity primary key,
    execucao_id    bigint  not null references public.execucoes_treino (id) on delete cascade,
    exercicio_id   integer not null references public.exercicios (id),
    numero_serie   smallint not null,
    repeticoes     smallint not null,
    carga_kg       numeric(6, 2),
    registrado_em  timestamptz not null default now()
);

-- ---------------------------------------------------------------------
-- Comunidade e grupos
-- ---------------------------------------------------------------------
create table public.posts (
    id         bigint generated always as identity primary key,
    autor_id   uuid not null references public.perfis (id) on delete cascade,
    categoria  text,
    texto      text not null check (length(trim(texto)) > 0),
    criado_em  timestamptz not null default now()
);

create table public.curtidas (
    post_id    bigint not null references public.posts (id) on delete cascade,
    usuario_id uuid   not null references public.perfis (id) on delete cascade,
    criado_em  timestamptz not null default now(),
    primary key (post_id, usuario_id)
);

create table public.comentarios (
    id         bigint generated always as identity primary key,
    post_id    bigint not null references public.posts (id) on delete cascade,
    autor_id   uuid   not null references public.perfis (id) on delete cascade,
    texto      text   not null check (length(trim(texto)) > 0),
    criado_em  timestamptz not null default now()
);

create table public.grupos (
    id           bigint generated always as identity primary key,
    nome         text not null,
    descricao    text not null default '',
    privacidade  public.privacidade_grupo not null default 'publico',
    criador_id   uuid not null references public.perfis (id) on delete cascade,
    criado_em    timestamptz not null default now()
);

create table public.membros_grupo (
    grupo_id    bigint not null references public.grupos (id) on delete cascade,
    usuario_id  uuid   not null references public.perfis (id) on delete cascade,
    entrou_em   timestamptz not null default now(),
    primary key (grupo_id, usuario_id)
);

-- Índices para as consultas mais comuns
create index on public.equipamentos (setor_id);
create index on public.relatos_problema (equipamento_id);
create index on public.fichas_treino (aluno_id);
create index on public.itens_ficha (ficha_id);
create index on public.execucoes_treino (usuario_id, inicio desc);
create index on public.series_executadas (execucao_id);
create index on public.posts (criado_em desc);
create index on public.comentarios (post_id);

-- =====================================================================
-- Funções auxiliares
-- =====================================================================

-- Retorna true se o usuário logado for instrutor.
-- "security definer" evita recursão nas políticas de RLS da tabela perfis.
create or replace function public.eh_instrutor()
returns boolean
language sql stable security definer set search_path = public
as $$
    select exists (
        select 1 from public.perfis
        where id = auth.uid() and perfil = 'instrutor'
    );
$$;

create or replace function public.eh_membro(p_grupo bigint)
returns boolean
language sql stable security definer set search_path = public
as $$
    select exists (
        select 1 from public.membros_grupo
        where grupo_id = p_grupo and usuario_id = auth.uid()
    );
$$;

-- Atualiza "atualizado_em" em perfis
create or replace function public.tocar_atualizado_em()
returns trigger language plpgsql as $$
begin
    new.atualizado_em := now();
    return new;
end $$;

create trigger perfis_atualizado_em
    before update on public.perfis
    for each row execute function public.tocar_atualizado_em();

-- ---------------------------------------------------------------------
-- Cadastro: cria o perfil e as respostas de prontidão a partir dos dados
-- enviados pelo app no signUp (campo "data" / raw_user_meta_data).
-- O perfil sempre nasce como 'aluno'; instrutores são promovidos no painel.
-- Depois de copiar, CPF e respostas de saúde são removidos dos metadados
-- da conta para não ficarem duplicados (LGPD).
-- ---------------------------------------------------------------------
create or replace function public.criar_perfil_novo_usuario()
returns trigger
language plpgsql security definer set search_path = public
as $$
declare
    m jsonb := coalesce(new.raw_user_meta_data, '{}'::jsonb);
begin
    insert into public.perfis (
        id, nome, email, matricula, cpf, curso,
        data_nascimento, genero, peso_kg, altura_cm, contato_emergencia
    ) values (
        new.id,
        coalesce(nullif(trim(m ->> 'nome'), ''), split_part(new.email, '@', 1)),
        new.email,
        nullif(trim(m ->> 'matricula'), ''),
        nullif(regexp_replace(coalesce(m ->> 'cpf', ''), '\D', '', 'g'), ''),
        nullif(trim(m ->> 'curso'), ''),
        case when (m ->> 'data_nascimento') ~ '^\d{4}-\d{2}-\d{2}$'
             then (m ->> 'data_nascimento')::date end,
        nullif(trim(m ->> 'genero'), ''),
        case when (m ->> 'peso_kg') ~ '^\d{1,3}(\.\d{1,2})?$'
             and (m ->> 'peso_kg')::numeric between 20 and 400
             then (m ->> 'peso_kg')::numeric end,
        case when (m ->> 'altura_cm') ~ '^\d{2,3}(\.\d)?$'
             and (m ->> 'altura_cm')::numeric between 80 and 260
             then (m ->> 'altura_cm')::numeric end,
        nullif(trim(m ->> 'contato_emergencia'), '')
    );

    -- "prontidao": { "1": true, "2": false, ... }
    insert into public.respostas_prontidao (usuario_id, pergunta_id, resposta)
    select new.id, r.key::smallint, r.value::boolean
    from jsonb_each_text(coalesce(m -> 'prontidao', '{}'::jsonb)) as r
    where r.key ~ '^\d+$'
      and r.value in ('true', 'false')
      and exists (select 1 from public.perguntas_prontidao p where p.id = r.key::smallint);

    -- Se o projeto não permitir alterar auth.users, o cadastro continua normalmente.
    begin
        update auth.users
           set raw_user_meta_data = m - 'cpf' - 'prontidao'
         where id = new.id;
    exception when others then
        null;
    end;

    return new;
end $$;

create trigger ao_criar_usuario
    after insert on auth.users
    for each row execute function public.criar_perfil_novo_usuario();

-- ---------------------------------------------------------------------
-- Ocupação de equipamentos
-- Antes de iniciar um uso: bloqueia se estiver em manutenção ou lotado e
-- calcula o fim previsto pelo tempo máximo do equipamento.
-- Depois de iniciar/finalizar: recalcula o status (disponível/ocupado).
-- ---------------------------------------------------------------------
create or replace function public.validar_inicio_utilizacao()
returns trigger
language plpgsql security definer set search_path = public
as $$
declare
    eq      public.equipamentos%rowtype;
    ativos  integer;
begin
    select * into eq from public.equipamentos where id = new.equipamento_id for update;

    if eq.status = 'manutencao' then
        raise exception 'Equipamento % está em manutenção.', eq.nome
            using errcode = 'P0001';
    end if;

    select count(*) into ativos
      from public.utilizacoes
     where equipamento_id = new.equipamento_id and fim is null;

    if ativos >= eq.capacidade_simultanea then
        raise exception 'Equipamento % está ocupado.', eq.nome
            using errcode = 'P0001';
    end if;

    new.inicio       := coalesce(new.inicio, now());
    new.fim_previsto := new.inicio + make_interval(mins => eq.tempo_maximo_min);
    new.fim          := null;
    return new;
end $$;

create trigger utilizacoes_validar_inicio
    before insert on public.utilizacoes
    for each row execute function public.validar_inicio_utilizacao();

create or replace function public.recalcular_status_equipamento()
returns trigger
language plpgsql security definer set search_path = public
as $$
declare
    v_equip integer := coalesce(new.equipamento_id, old.equipamento_id);
begin
    update public.equipamentos e
       set status = case
               when (select count(*) from public.utilizacoes u
                      where u.equipamento_id = e.id and u.fim is null)
                    >= e.capacidade_simultanea
               then 'ocupado'::public.status_equipamento
               else 'disponivel'::public.status_equipamento
           end
     where e.id = v_equip
       and e.status <> 'manutencao';
    return null;
end $$;

create trigger utilizacoes_recalcular_status
    after insert or update of fim or delete on public.utilizacoes
    for each row execute function public.recalcular_status_equipamento();

-- Usos que passaram do tempo máximo são encerrados automaticamente
-- quando alguém consulta (chamada pelo app via RPC).
create or replace function public.encerrar_utilizacoes_expiradas()
returns integer
language plpgsql security definer set search_path = public
as $$
declare
    qtd integer;
begin
    update public.utilizacoes
       set fim = fim_previsto
     where fim is null and fim_previsto < now();
    get diagnostics qtd = row_count;
    return qtd;
end $$;

-- =====================================================================
-- Views (consultas prontas para as telas)
-- =====================================================================

-- Dados públicos de um usuário (nome e curso) para comunidade, grupos etc.
-- Não expõe CPF, saúde, peso ou e-mail.
create or replace view public.perfis_publicos as
    select id, nome, curso, perfil from public.perfis;

-- Tela "Academia agora" e "Mapa da academia"
create or replace view public.ocupacao_setores
with (security_invoker = true) as
    select s.id,
           s.nome,
           s.icone,
           case
               when s.em_manutencao then 'manutencao'
               when count(e.id) filter (where e.status = 'disponivel') = 0
                    and count(e.id) > 0 then 'ocupado'
               else 'disponivel'
           end as status,
           count(e.id)                                         as total_equipamentos,
           count(e.id) filter (where e.status = 'disponivel')  as disponiveis,
           count(e.id) filter (where e.status = 'ocupado')     as ocupados,
           count(e.id) filter (where e.status = 'manutencao')  as em_manutencao_qtd
      from public.setores s
      left join public.equipamentos e on e.setor_id = s.id
     group by s.id;

-- Lista de equipamentos já com o nome do setor
create or replace view public.equipamentos_detalhados
with (security_invoker = true) as
    select e.*, s.nome as setor_nome
      from public.equipamentos e
      join public.setores s on s.id = e.setor_id;

-- Feed da comunidade com autor, total de curtidas e de comentários
create or replace view public.feed_posts as
    select p.id,
           p.autor_id,
           a.nome as autor_nome,
           p.categoria,
           p.texto,
           p.criado_em,
           (select count(*) from public.curtidas c   where c.post_id = p.id) as curtidas,
           (select count(*) from public.comentarios c where c.post_id = p.id) as comentarios,
           exists (select 1 from public.curtidas c
                    where c.post_id = p.id and c.usuario_id = auth.uid())    as curtido_por_mim
      from public.posts p
      join public.perfis a on a.id = p.autor_id;

-- Evolução de carga por mês (tela Evolução)
create or replace view public.evolucao_carga_mensal
with (security_invoker = true) as
    select x.usuario_id,
           date_trunc('month', s.registrado_em)::date as mes,
           max(s.carga_kg)                            as carga_max_kg,
           sum(s.carga_kg * s.repeticoes)             as volume_kg
      from public.series_executadas s
      join public.execucoes_treino x on x.id = s.execucao_id
     group by x.usuario_id, date_trunc('month', s.registrado_em);
