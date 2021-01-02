import { GridOptions, IServerSideGetRowsParams, IServerSideGetRowsRequest } from 'ag-grid-community'
import { TABLE__ADD_FETCH_ROW_FOR_SCROLL } from 'config/constants'
import { DataType } from 'model'
import { fetchData } from 'util/api'

class ServerSideDatasource {
  gridOptions: GridOptions
  type: DataType
  dataCount?: number
  request?: IServerSideGetRowsRequest

  constructor(gridOptions: GridOptions, type: DataType) {
    this.gridOptions = {...gridOptions}
    this.type = type
  }

  async getRows({request, successCallback, failCallback, api}: IServerSideGetRowsParams) {
    request.endRow += TABLE__ADD_FETCH_ROW_FOR_SCROLL
    this.request = request
    try {
      const data = await fetchData(this.type, request)
      if (!!data) {
        this.dataCount = data.count
        const rows = data.data
        const lastRow = data.count
        if (lastRow === 0) {
          api.showNoRowsOverlay()
        }
        successCallback(rows, lastRow)
      }
    } catch (e) {
      this.dataCount = 0
      failCallback()
    }
  }
}

export default ServerSideDatasource
