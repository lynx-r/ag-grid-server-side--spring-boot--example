import { GridOptions } from 'ag-grid-community'
import { defaultColDef } from './commonOptions'

export const employeesGridOptions: GridOptions = {
  columnDefs: [
    {field: 'empNo', filter: 'agNumberColumnFilter'},
    // {field: 'salariesFromDate', rowGroup: true, hide: true},
    {field: 'firstName', filter: 'agTextColumnFilter'},
    {field: 'lastName', filter: 'agTextColumnFilter'},
    {field: 'gender', filter: 'agTextColumnFilter'},
    {field: 'hireDate', filter: 'agDateColumnFilter'},
    {field: 'birthDate', filter: 'agDateColumnFilter'},
    {field: 'totalSalary', filter: false},
    {field: 'salariesAsString', headerName: 'Salaries', filter: 'agNumberColumnFilter'},
  ],

  rowModelType: 'serverSide',

  // defaultColDef: {
  //   sortable: true
  // }

  // noRowsOverlayComponent: 'noEmployeesRowsOverlay',
  defaultColDef: {...defaultColDef},
  // ...commonSettings
}
