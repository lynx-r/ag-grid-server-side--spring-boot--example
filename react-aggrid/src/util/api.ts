import { IServerSideGetRowsRequest } from 'ag-grid-community'
import axios from 'axios'
import { BASE_URL } from 'config/apiUrls'
import { DataResponse, DataType } from 'model'

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  responseType: 'json',
})

export const fetchData = (type: DataType, req: IServerSideGetRowsRequest): Promise<DataResponse> =>
  axiosInstance.post(`/${type}`, req).then(res => res.data)
