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
            u.country_iso2 region,
            min(best) region_best
        from
            Events e
            inner join RanksSingle r on e.id = r.eventId
            inner join users u on r.personId = u.wca_id
        where
            e.`rank` < 900
        group by
            e.id,
            u.country_iso2
    )
select
    c.name region,
    (
        select
            'Country'
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
    left join Countries c on c.iso2 = u.country_iso2
    left join region_bests rb on rb.event_id = e.id
    and rb.region = c.id
where
    e.`rank` < 900 -- Filter by active ranks
    and wca_id is not null
group by
    wca_id