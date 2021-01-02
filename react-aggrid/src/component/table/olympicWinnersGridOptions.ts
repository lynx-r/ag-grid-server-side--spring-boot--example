import { GridOptions } from 'ag-grid-community'
import {
  commonSettings,
  defaultColDef,
  defaultFilterParams,
  numberFilterOptions,
  textFilterOptions
} from './commonOptions'

export const olympicWinnersGridOptions: GridOptions = {
  columnDefs: [
    {
      headerName: 'Athlete',
      field: 'athlete',
      filter: 'agTextColumnFilter',
      filterParams: {
        ...defaultFilterParams,
        filterOptions: textFilterOptions,
      },
    },
    {
      headerName: 'Country',
      field: 'country', enableRowGroup: true, rowGroup: true, hide: true
    },
    {
      headerName: 'Sport',
      field: 'sport', enableRowGroup: true, rowGroup: true, hide: true
    },
    {
      headerName: 'Year',
      field: 'year',
      filter: 'agNumberColumnFilter',
      filterParams: {
        ...defaultFilterParams,
        filterOptions: numberFilterOptions
      },
    },
    {
      headerName: 'Age',
      field: 'age',
      filter: 'agNumberColumnFilter',
      filterParams: {
        ...defaultFilterParams,
        filterOptions: numberFilterOptions
      },
    },
    {
      headerName: 'Gold',
      field: 'gold',
      type: 'valueColumn',
      filter: 'agNumberColumnFilter',
      filterParams: {
        ...defaultFilterParams,
        filterOptions: numberFilterOptions
      },
    },
    {
      headerName: 'Silver',
      field: 'silver',
      type: 'valueColumn',
      filter: 'agNumberColumnFilter',
      filterParams: {
        ...defaultFilterParams,
        filterOptions: numberFilterOptions
      },
    },
    {
      headerName: 'Bronze',
      field: 'bronze',
      type: 'valueColumn',
      filter: 'agNumberColumnFilter',
      filterParams: {
        ...defaultFilterParams,
        filterOptions: numberFilterOptions
      },
    },
  ],
  noRowsOverlayComponent: 'noOlympicWinnersRowsOverlay',
  defaultColDef: {...defaultColDef},
  ...commonSettings
}
