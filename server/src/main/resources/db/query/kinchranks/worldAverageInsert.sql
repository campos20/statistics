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
                    RanksAverage r
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
            'Average'
    ) result_type,
    avg(coalesce(best, 0) / region_best) overall,
    json_arrayagg(
        json_object(
            'event',
            json_object('id', e.id, 'name', e.name, 'rank', e.rank),
            'regionalRank',
            coalesce(best, 0) / region_best,
            'completed',
            r.worldRank is not null
        )
    ) events
from
    Events e
    left join users u on true
    left join RanksAverage r on r.eventId = e.id
    and r.personId = u.wca_id
    left join region_bests rb on rb.event_id = e.id
where
    e.`rank` < 900 -- Filter by active ranks
    and wca_id is not null
    and exists (
        -- Some events has no averages and this excludes them to avoid adding 1 into the sum
        select
            1
        from
            RanksAverage ra2
        where
            ra2.eventId = e.id
    )
group by
    wca_id