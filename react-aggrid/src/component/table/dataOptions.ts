import { GridOptions } from 'ag-grid-community'
import { employeesGridOptions } from 'component/table/employeesGridOptions'
import { olympicWinnersGridOptions } from './olympicWinnersGridOptions'
import { DataType } from 'model'

const ANALYTIC_GRID_OPTIONS: { [key in DataType]: GridOptions } = {
  [DataType.OLYMPIC_WINNER]: olympicWinnersGridOptions,
  [DataType.EMPLOYEES]: employeesGridOptions,
}

export default ANALYTIC_GRID_OPTIONS
