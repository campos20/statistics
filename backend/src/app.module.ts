import { Module } from '@nestjs/common';
import { StatisticsModule } from './statistics/statistics.module';
import { SumOfRanksModule } from './sum-of-ranks/sum-of-ranks.module';
import { BestEverRanksModule } from './best-ever-ranks/best-ever-ranks.module';
import { WcaModule } from './wca/wca.module';
import { RecordEvolutionModule } from './record-evolution/record-evolution.module';

@Module({
  imports: [StatisticsModule, SumOfRanksModule, BestEverRanksModule, WcaModule, RecordEvolutionModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
