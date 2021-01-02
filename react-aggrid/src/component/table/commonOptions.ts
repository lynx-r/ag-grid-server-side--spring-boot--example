import { ColDef, GridOptions } from 'ag-grid-community'
import { NO_OLYMPIC_WINNERS } from 'config/messages'

import { headerValueGetter } from 'util/aggridUtils'
import { default as customNoRowsOverlayFactory } from './customNoRowsOverlay'

export const numberFilterOptions = ['equals', 'lessThanOrEqual', 'greaterThanOrEqual']
export const dateFilterOptions = ['equals', 'lessThanOrEqual', 'greaterThanOrEqual']
export const textFilterOptions = ['contains']

export const defaultFilterParams = {
  buttons: ['clear'],
  defaultToNothingSelected: true,
}

// todo вместо этих кнопок для Set фильтра
//  https://www.ag-grid.com/javascript-grid-filter-set-excel-mode/
export const defaultSetFilterParams = {
  buttons: ['clear', 'apply'],
  defaultToNothingSelected: true,
}

export const defaultColDef: ColDef = {
  floatingFilter: true,
  filter: true,
  sortable: true,
  headerValueGetter: headerValueGetter,
  filterParams: {
    newRowsAction: 'keep',
    debounceMs: 1500,
    buttons: ['clear'],
    suppressAndOrCondition: true,
  },
}

export const commonSettings: GridOptions = {
  tooltipShowDelay: 1000,
  enableRangeSelection: true,
  rowModelType: 'serverSide',
  sideBar: {
    toolPanels: ['columns', 'filters'],
    defaultToolPanel: ''
  },
  frameworkComponents: {
    noOlympicWinnersRowsOverlay: customNoRowsOverlayFactory(NO_OLYMPIC_WINNERS),
  },
}
