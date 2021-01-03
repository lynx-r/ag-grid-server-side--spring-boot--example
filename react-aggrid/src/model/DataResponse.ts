import { OlympicWinner } from './OlympicWinner'

export type Data = OlympicWinner

export interface DataResponse {
  lastRow: number
  rows: Data[]
}
