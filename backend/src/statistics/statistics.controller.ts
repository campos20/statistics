import { Controller, Get, Param } from '@nestjs/common';
import { StatisticsService } from './statistics.service';

@Controller('statistics')
export class StatisticsController {
  constructor(private readonly appService: StatisticsService) {}

  @Get()
  list(term?: string) {
    return this.appService.list(term);
  }

  @Get(':pathId')
  detail(@Param('pathId') pathId: string) {
    return this.appService.detail(pathId);
  }
}
