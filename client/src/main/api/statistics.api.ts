import Axios from "axios";
import { AuthenticationResponse } from "../model/AuthenticationResponse";
import BestEverRank from "../model/BestEverRank";
import { Statistics } from "../model/Statistic";
import { StatisticsList } from "../model/StatisticsList";
import UserInfo from "../model/UserInfo";
import { API_URL } from "../config/EnvVarConfig";

export class StatisticsApi {
  BASE_URL: string;
  queryDatabaseEndpoint: string;

  constructor() {
    this.BASE_URL = API_URL!;
    this.queryDatabaseEndpoint = "/database/query";
  }

  getWcaAuthenticationUrl = (frontendHost: string, redirect: string) =>
    Axios.get<AuthenticationResponse>(this.BASE_URL + "/wca/authentication", {
      params: { frontendHost, redirect },
    });

  getUserInfo = () => Axios.get<UserInfo>(this.BASE_URL + "/wca/user");

  getStatisticsGroups = (term?: string) =>
    Axios.get<StatisticsList>(this.BASE_URL + "/statistics/list", {
      params: { term },
    });

  getStatistic = (pathId: string) =>
    Axios.get<Statistics>(this.BASE_URL + "/statistics/list/" + pathId);

  getRanks = (wcaId: string) =>
    Axios.get<BestEverRank>(this.BASE_URL + "/best-ever-rank/" + wcaId);
}

const statisticsApi = new StatisticsApi();
export default statisticsApi;
