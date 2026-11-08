-- =====================================================================
-- GYEMA — Banco de dados (Supabase / PostgreSQL)
-- Arquivo 3 de 3: dados iniciais (os mesmos que hoje estão fixos nas telas)
-- =====================================================================

insert into public.perguntas_prontidao (id, texto) values
    (1,  'Já praticou musculação ou treinamento funcional?'),
    (2,  'Já sofreu alguma lesão muscular?'),
    (3,  'Apresenta alguma dor muscular ou articular?'),
    (4,  'Fez alguma cirurgia nos últimos meses?'),
    (5,  'Faz uso de algum medicamento?'),
    (6,  'Faz exames cardiológicos?'),
    (7,  'Tem pressão alta?'),
    (8,  'Tem diabetes?'),
    (9,  'Apresenta alguma restrição física ou dolorosa?'),
    (10, 'Já se sentiu mal durante a prática esportiva?');

insert into public.setores (nome, icone, em_manutencao) values
    ('Musculação', 'FitnessCenter',    false),
    ('Cardio',     'MonitorHeart',     false),
    ('Alongar',    'SelfImprovement',  false),
    ('Funcional',  'BusinessCenter',   true);

insert into public.exercicios (nome, grupo_muscular) values
    ('Supino reto',        'Peito'),
    ('Supino inclinado',   'Peito'),
    ('Crucifixo',          'Peito'),
    ('Tríceps corda',      'Tríceps'),
    ('Tríceps barra reta', 'Tríceps'),
    ('Puxada frontal',     'Costas'),
    ('Remada baixa',       'Costas'),
    ('Rosca direta',       'Bíceps'),
    ('Agachamento livre',  'Pernas'),
    ('Agachamento no Smith','Pernas'),
    ('Leg press 45°',      'Pernas'),
    ('Hack squat',         'Pernas'),
    ('Cadeira extensora',  'Pernas'),
    ('Desenvolvimento',    'Ombros'),
    ('Elevação lateral',   'Ombros'),
    ('Abdominal',          'Abdômen'),
    ('Corrida na esteira', 'Cardio');

insert into public.equipamentos
    (codigo, nome, setor_id, grupo_muscular, capacidade_simultanea, tempo_maximo_min, status, instrucoes_uso)
values
    ('SUP-01', 'Supino Reto',   (select id from public.setores where nome = 'Musculação'), 'Peito',  1, 20, 'disponivel',
        'Ajuste o banco, mantenha os pés apoiados e desça a barra até a linha do peito.'),
    ('SMT-01', 'Smith Machine', (select id from public.setores where nome = 'Musculação'), 'Pernas', 1, 20, 'disponivel',
        'Destrave a barra girando o punho e use as travas de segurança.'),
    ('EST-03', 'Esteira 3',     (select id from public.setores where nome = 'Cardio'),     'Cardio', 1, 30, 'disponivel',
        'Prenda o clipe de segurança na roupa antes de iniciar.'),
    ('HCK-01', 'Hack Squat',    (select id from public.setores where nome = 'Musculação'), 'Pernas', 1, 20, 'manutencao',
        'Mantenha as costas apoiadas no encosto durante todo o movimento.'),
    ('EQ-023', 'Leg Press 45°', (select id from public.setores where nome = 'Musculação'), 'Pernas', 1, 15, 'disponivel',
        'Não estenda totalmente os joelhos no final do movimento.');

insert into public.equipamento_exercicios (equipamento_id, exercicio_id, tipo)
select e.id, x.id, v.tipo::public.tipo_exercicio_eq
from (values
        ('SUP-01', 'Supino reto',          'principal'),
        ('SUP-01', 'Supino inclinado',     'alternativo'),
        ('SMT-01', 'Agachamento no Smith', 'principal'),
        ('SMT-01', 'Supino reto',          'alternativo'),
        ('EST-03', 'Corrida na esteira',   'principal'),
        ('HCK-01', 'Hack squat',           'principal'),
        ('HCK-01', 'Leg press 45°',        'alternativo'),
        ('EQ-023', 'Leg press 45°',        'principal'),
        ('EQ-023', 'Hack squat',           'alternativo')
     ) as v(codigo, exercicio, tipo)
join public.equipamentos e on e.codigo = v.codigo
join public.exercicios   x on x.nome   = v.exercicio;

-- ---------------------------------------------------------------------
-- Como transformar uma conta em INSTRUTOR (rode depois que a pessoa se
-- cadastrar pelo app, trocando o e-mail):
--
--   update public.perfis set perfil = 'instrutor'
--    where email = 'instrutor@unifor.br';
-- ---------------------------------------------------------------------
