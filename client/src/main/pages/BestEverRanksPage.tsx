import {
  LoadingOutlined,
  QuestionCircleOutlined,
  UserOutlined,
} from "@ant-design/icons";
import "@cubing/icons";
import { Col, Form, Input, Popover, Row, Tag, Tooltip, message } from "antd";
import React, { useCallback, useEffect, useState } from "react";
import statisticsApi from "../api/statistics.api";
import BestEverRank from "../model/BestEverRank";
import Competitor from "../model/Competitor";
import Results from "../model/Results";
import { getQueryParameter, setQueryParameter } from "../util/query.param.util";
import formatResult, { ResultType } from "../util/result.util";
import "./BestEverRanksPage.css";

const BestEverRanksPage = () => {
  const [wcaId, setWcaId] = useState(getQueryParameter("wcaId") || "");
  const [bestEverRank, setBestEverRank] = useState<BestEverRank>();
  const [showingFor, setShowingFor] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = useCallback((wcaId: string) => {
    setLoading(true);
    setQueryParameter("wcaId", wcaId);
    statisticsApi
      .getRanks(wcaId)
      .then((response) => {
        setBestEverRank(response.data);
        setShowingFor(wcaId);
      })
      .catch(() => message.error("Failed to search."))
      .finally(() => setLoading(false));
  }, []);

  const getRank = (rank: number) => {
    return rank != null ? (
      <span className={rank < 10 ? "top-10-rank" : ""}>{rank + 1}</span>
    ) : null;
  };

  const getCompetitionLink = (
    compeitionId: string,
    competitionName?: string,
  ) => (
    <a
      href={`https://www.worldcubeassociation.org/competitions/${compeitionId}`}
    >
      {competitionName || compeitionId}
    </a>
  );

  const getResults = (results: Results, event_id: string, type: ResultType) => (
    <>
      <td>{formatResult(results.bestRank.result, event_id, type)}</td>
      <td>{getRank(results.bestRank.rank)}</td>
      <td>{results.bestRank.start}</td>
      <td>{results.bestRank.end}</td>
      <td>{getCompetitionLink(results.bestRank.competition)}</td>
    </>
  );

  const getCompetitor = (
    competitor: Competitor,
    event_id: string,
    rank_type: string,
    tooltip?: string,
  ) => (
    <>
      <th>
        <Tooltip title={tooltip}>{rank_type}</Tooltip>
      </th>
      {getResults(competitor.single, event_id, "single")}
      {getResults(competitor.average, event_id, "average")}
    </>
  );

  const getResultsHeaders = () => {
    return (
      <>
        <th scope="row">Result</th>
        <th scope="row">Best Rank</th>
        <th scope="row">Start</th>
        <th scope="row">End</th>
        <th scope="row">Competition</th>
      </>
    );
  };

  // If we find an ID, already fetch results
  useEffect(() => {
    let wcaId = getQueryParameter("wcaId");
    if (!!wcaId) {
      handleSubmit(wcaId);
    }
  }, [handleSubmit]);

  return (
    <div className="ranks-wrapper">
      <h1 className="page-title">Best Ever Ranks</h1>
      <Form onFinish={() => handleSubmit(wcaId)}>
        <Row>
          <Col span={7} />
          <Col xs={24} md={10}>
            <Form.Item>
              <Input
                prefix={loading ? <LoadingOutlined /> : <UserOutlined />}
                placeholder="WCA ID"
                value={wcaId}
                onChange={(e) => setWcaId(e.target.value)}
                minLength={10}
                maxLength={10}
                required
              />
            </Form.Item>
          </Col>
        </Row>
      </Form>
      {!!bestEverRank && (
        <table className="ranks-table">
          <thead>
            <tr>
              <td colSpan={12} className="showing-for">
                <Tag>
                  Showing results for <strong>{showingFor}</strong>{" "}
                  <Popover content="Results are considered at the end of the day, and the first day of the competition is assumed. Thus some values might be slightly off, but this page is just intended to show the big picture anyway.">
                    <QuestionCircleOutlined />
                  </Popover>
                </Tag>
              </td>
            </tr>
            <tr>
              <th colSpan={2}></th>
              <th scope="col" colSpan={5}>
                Single
              </th>
              <th scope="col" colSpan={5}>
                Average
              </th>
            </tr>
            <tr>
              <th scope="row">Event</th>
              <th scope="row">Rank</th>
              {getResultsHeaders()}
              {getResultsHeaders()}
            </tr>
          </thead>
          <tbody>
            {bestEverRank.eventRanks.map((eventRank) => (
              <React.Fragment key={eventRank.event.id}>
                {eventRank.worlds.map((world, i) => (
                  <tr key={i}>
                    <th
                      rowSpan={
                        eventRank.worlds.length +
                        eventRank.continents.length +
                        eventRank.countries.length
                      }
                    >
                      <span
                        className={`cubing-icon event-${eventRank.event.id}`}
                      />
                      <p className="event-name">{eventRank.event.name}</p>
                    </th>
                    {getCompetitor(world, eventRank.event.id, "WR")}
                  </tr>
                ))}
                {eventRank.continents.map((continent) => (
                  <tr key={continent.continent}>
                    {getCompetitor(
                      continent,
                      eventRank.event.id,
                      "CR",
                      continent.continent,
                    )}
                  </tr>
                ))}
                {eventRank.countries.map((country) => (
                  <tr key={country.country}>
                    {getCompetitor(
                      country,
                      eventRank.event.id,
                      "NR",
                      country.country,
                    )}
                  </tr>
                ))}
                <tr className="empty-row">
                  <th colSpan={12}></th>
                </tr>
              </React.Fragment>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default BestEverRanksPage;
