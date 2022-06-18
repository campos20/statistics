insert into
    kinch_ranks (
        region,
        region_type,
        wca_id,
        name,
        country_iso2,
        result_type,
        overall,
        events
    ) with region_bests as (
        select
            e.id event_id,
            (
                select
                    min(best)
                from
                    RanksSingle r
                where
                    r.eventId = e.id
            ) region_best
        from
            Events e
        where
            e.`rank` < 900
    )
select
    (
        select
            'World'
    ) region,
    (
        select
            'World'
    ) region_type,
    wca_id,
    u.name,
    country_iso2,
    (
        select
            'Single'
    ) result_type,
    round(
        avg(
            case
                when coalesce(best, 0) = 0 then 0
                else region_best / best
            end * 100
        ),
        2
    ) overall,
    json_arrayagg(
        json_object(
            'event',
            json_object('id', e.id, 'name', e.name, 'rank', e.rank),
            'regionalRank',
            case
                when coalesce(best, 0) = 0 then 0
                else region_best / best
            end * 100
        )
    ) events
from
    Events e
    left join users u on true
    left join RanksSingle r on r.eventId = e.id
    and r.personId = u.wca_id
    left join region_bests rb on rb.event_id = e.id
where
    e.`rank` < 900 -- Filter by active ranks
    and wca_id is not null
group by
    wca_id