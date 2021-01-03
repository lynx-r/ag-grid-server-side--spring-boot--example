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
    {field: 'athlete', filter: 'agTextColumnFilter'},
    {field: 'country', rowGroup: true, hide: true},
    {field: 'sport', rowGroup: true, hide: true},
    {field: 'year', filter: 'agNumberColumnFilter'},
    {field: 'gold', filter: 'agNumberColumnFilter', aggFunc: 'sum'},
    {field: 'silver', filter: 'agNumberColumnFilter', aggFunc: 'sum'},
    {field: 'bronze', filter: 'agNumberColumnFilter', aggFunc: 'sum'},

    // {
    //   headerName: 'Athlete',
    //   field: 'athlete',
    //   filter: 'agTextColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: textFilterOptions,
    //   },
    // },
    // {
    //   headerName: 'Country',
    //   field: 'country', enableRowGroup: true, rowGroup: true, hide: true
    // },
    // {
    //   headerName: 'Sport',
    //   field: 'sport', enableRowGroup: true, rowGroup: true, hide: true
    // },
    // {
    //   headerName: 'Year',
    //   field: 'year',
    //   filter: 'agNumberColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: numberFilterOptions
    //   },
    // },
    // {
    //   headerName: 'Age',
    //   field: 'age',
    //   filter: 'agNumberColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: numberFilterOptions
    //   },
    // },
    // {
    //   headerName: 'Gold',
    //   field: 'gold',
    //   filter: 'agNumberColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: numberFilterOptions
    //   },
    // },
    // {
    //   headerName: 'Silver',
    //   field: 'silver',
    //   filter: 'agNumberColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: numberFilterOptions
    //   },
    // },
    // {
    //   headerName: 'Bronze',
    //   field: 'bronze',
    //   filter: 'agNumberColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: numberFilterOptions
    //   },
    // },
    // {
    //   headerName: 'Total',
    //   field: 'total',
    //   filter: 'agNumberColumnFilter',
    //   filterParams: {
    //     ...defaultFilterParams,
    //     filterOptions: numberFilterOptions
    //   },
    // },
  ],

  rowModelType: 'serverSide',

  // defaultColDef: {
  //   sortable: true
  // }

  // noRowsOverlayComponent: 'noOlympicWinnersRowsOverlay',
  defaultColDef: {...defaultColDef},
  // ...commonSettings
}
