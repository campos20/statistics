insert into chart_race (event_id, data)
values (:EVENT_ID, :DATA) on duplicate key
update data = json_array_append(
        data,
        '$',
        json_extract(
            values(data),
                '$[0]'
        )
    )
