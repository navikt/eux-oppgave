create table eux_oppgave_status
(
    oppgave_uuid        uuid primary key not null,
    oppgave_id          integer,
    tema                text             not null,
    status              text             not null,
    beskrivelse         text,
    opprettet_bruker    text             not null,
    opprettet_tidspunkt timestamp        not null,
    endret_tidspunkt    timestamp        not null,
    constraint unique (oppgave_id)
);
