import { Injectable } from '@nestjs/common';

@Injectable()
export class StatisticsService {
  list(term?: string) {
    return [];
  }

  detail(pathId: string) {
    return { pathId };
  }
}
