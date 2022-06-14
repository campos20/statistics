import { message, Select, Skeleton } from "antd";
import { useCallback, useEffect, useState } from "react";
import { sumOfRanksApi } from "../../api/SumOfRanksApi";
import { SorResultType } from "../../model/rank/MetaSorInfo";
import { SumOfRanks } from "../../model/rank/SumOfRanks";
import styles from "./SumOfRanksPage.module.css";

const INITIAL_PAGE_SIZE = 20;

export const SumOfRanksPage = () => {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(INITIAL_PAGE_SIZE);
  const [region, setRegion] = useState("World");
  const [regionType, setRegionType] = useState("World");
  const [ranks, setRanks] = useState<SumOfRanks[]>([]);
  const [loading, setLoading] = useState(false);
  const [resultTypes, setResultTypes] = useState<SorResultType[]>([]);
  const [resultType, setResultType] = useState<string>();
  const [availableEvents, setAvailableEvents] = useState<Event[]>([]);

  const fetchMetaInfor = useCallback(() => {
    sumOfRanksApi
      .meta()
      .then((response) => {
        setResultTypes(response.data.resultTypes);
        setResultType(response.data.resultTypes[0].resultType);
      })
      .catch(() => {
        message.error("Error searching for Sum of Ranks information");
      });
  }, []);

  const fetchSumOfRanks = useCallback(
    (
      region: string,
      regionType: string,
      resultType: string,
      page: number,
      pageSize: number
    ) => {
      setLoading(true);
      sumOfRanksApi
        .listSumOfRanks(region, regionType, resultType, page, pageSize)
        .then((response) => {
          setRanks(response.data);
        })
        .finally(() => setLoading(false));
    },
    []
  );

  useEffect(fetchMetaInfor, [fetchMetaInfor]);

  useEffect(
    () => fetchSumOfRanks("World", "World", "single", 0, INITIAL_PAGE_SIZE),
    [fetchSumOfRanks]
  );

  return (
    <>
      <h1 className="page-title">Sum of Ranks</h1>

      <Select
        className={styles.regionTypes}
        value={resultType}
        onChange={(e) => setResultType(e)}
        options={resultTypes.map((r) => ({
          label: r.resultType,
          value: r.resultType,
        }))}
      />
      <Skeleton loading={loading} />

      {ranks.length > 0 && (
        <div className={styles.ranksTable}>
          <table>
            <thead>
              <tr>
                <th>Rank</th>
                <th>Person</th>
                <th>Overall</th>
              </tr>
            </thead>
            <tbody>
              {ranks.map((r) => (
                <tr key={r.regionRank}>
                  <th>{r.regionRank}</th>
                  <td>{r.wcaId}</td>
                  <td>{r.overall}</td>
                  {r.events.map((e) => (
                    <td key={e.eventId}>{e.rank}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
};
