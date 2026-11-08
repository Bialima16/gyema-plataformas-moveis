-- =====================================================================
-- GYEMA — APAGA todo o banco do app (tabelas, views, funções e tipos).
-- Use só se precisar recomeçar; depois rode 01, 02 e 03 de novo.
-- As contas em Authentication → Users NÃO são apagadas.
-- =====================================================================

drop trigger if exists ao_criar_usuario on auth.users;

drop view if exists public.evolucao_carga_mensal, public.feed_posts,
    public.equipamentos_detalhados, public.ocupacao_setores, public.perfis_publicos cascade;

drop table if exists public.membros_grupo, public.grupos, public.comentarios,
    public.curtidas, public.posts, public.series_executadas, public.execucoes_treino,
    public.itens_ficha, public.fichas_treino, public.relatos_problema, public.utilizacoes,
    public.equipamento_exercicios, public.equipamentos, public.exercicios, public.setores,
    public.respostas_prontidao, public.perguntas_prontidao, public.perfis cascade;

drop function if exists public.eh_instrutor(), public.eh_membro(bigint),
    public.tocar_atualizado_em(), public.criar_perfil_novo_usuario(),
    public.validar_inicio_utilizacao(), public.recalcular_status_equipamento(),
    public.encerrar_utilizacoes_expiradas(), public.adicionar_criador_como_membro() cascade;

drop type if exists public.perfil_usuario, public.status_equipamento, public.status_relato,
    public.privacidade_grupo, public.tipo_exercicio_eq cascade;
