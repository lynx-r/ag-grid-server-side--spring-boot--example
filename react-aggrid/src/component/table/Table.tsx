// tslint:disable-next-line
import 'ag-grid-enterprise'
import { AgGridReact } from 'ag-grid-react'
import { DataType } from 'model'
import React, { useCallback, useMemo } from 'react'
import ServerSideDatasource from 'util/ServerSideDatasource'
import DATA_GRID_OPTIONS from './dataOptions'
import { default as TotalRows } from './TotalRows'

interface TableProps {
  type: DataType
}

const Table = ({type}: TableProps) => {
  const gridOptions = useMemo(() => DATA_GRID_OPTIONS[type], [type])
  const datasource = useMemo(() => new ServerSideDatasource(gridOptions, type), [gridOptions, type])

  const AgGridHoc = useCallback(
    () => <AgGridReact
      serverSideDatasource={datasource}
      gridOptions={gridOptions}
    />,
    [datasource, gridOptions])

  return (
    <div className="ag-theme-alpine" style={{width: 1200, height: 700}}>
      <TotalRows count={datasource?.dataCount}/>
      <AgGridHoc/>
    </div>
  )
}

export default Table
