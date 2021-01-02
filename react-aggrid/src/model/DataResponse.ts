import { OlympicWinner } from './OlympicWinner'

export type Data = OlympicWinner

export interface DataResponse {
  count: number
  data: Data[]
}
