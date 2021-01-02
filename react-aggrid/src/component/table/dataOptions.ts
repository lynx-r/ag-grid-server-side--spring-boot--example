import { GridOptions } from 'ag-grid-community'
import { olympicWinnersGridOptions } from './olympicWinnersGridOptions'
import { DataType } from 'model'

const ANALYTIC_GRID_OPTIONS: { [key in DataType]: GridOptions } = {
  [DataType.OLYMPIC_WINNER]: olympicWinnersGridOptions,
}

export default ANALYTIC_GRID_OPTIONS
