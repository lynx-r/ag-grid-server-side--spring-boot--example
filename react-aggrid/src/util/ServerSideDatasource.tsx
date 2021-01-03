import { GridOptions, IServerSideGetRowsParams, IServerSideGetRowsRequest } from 'ag-grid-community'
import { TABLE__ADD_FETCH_ROW_FOR_SCROLL } from 'config/constants'
import { DataType } from 'model'
import React from 'react'
import { fetchData } from 'util/api'

class ServerSideDatasource {
  gridOptions: GridOptions
  type: DataType
  request?: IServerSideGetRowsRequest
  setDataCount: React.Dispatch<React.SetStateAction<number | undefined>>

  constructor(gridOptions: GridOptions,
              type: DataType,
              setDataCount: React.Dispatch<React.SetStateAction<number | undefined>>
  ) {
    this.setDataCount = setDataCount
    this.gridOptions = {...gridOptions}
    this.type = type
  }

  async getRows({request, successCallback, failCallback, api}: IServerSideGetRowsParams) {
    this.request = request
    try {
      const data = await fetchData(this.type, request)
      if (!!data) {
        const rows = data.rows
        const lastRow = data.lastRow
        if (lastRow === 0) {
          api.showNoRowsOverlay()
        }
        this.setDataCount(data.lastRow)
        successCallback(rows, lastRow)
      }
    } catch (e) {
      this.setDataCount(0)
      failCallback()
    }
  }
}

export default ServerSideDatasource
