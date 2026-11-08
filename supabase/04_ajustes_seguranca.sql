-- =====================================================================
-- GYEMA — Arquivo 4: ajustes apontados pelo Security Advisor do Supabase
-- =====================================================================

-- Funções de gatilho não devem ser chamadas pela API (/rest/v1/rpc/...).
-- Os gatilhos continuam funcionando: o Postgres não exige EXECUTE para dispará-los.
revoke execute on function public.criar_perfil_novo_usuario()     from public, anon, authenticated;
revoke execute on function public.validar_inicio_utilizacao()     from public, anon, authenticated;
revoke execute on function public.recalcular_status_equipamento() from public, anon, authenticated;
revoke execute on function public.adicionar_criador_como_membro() from public, anon, authenticated;
revoke execute on function public.tocar_atualizado_em()           from public, anon, authenticated;

-- Usadas pelas regras de acesso: só quem está logado precisa delas.
revoke execute on function public.eh_instrutor()      from public, anon;
revoke execute on function public.eh_membro(bigint)   from public, anon;
grant  execute on function public.eh_instrutor()      to authenticated;
grant  execute on function public.eh_membro(bigint)   to authenticated;

alter function public.tocar_atualizado_em() set search_path = public;
