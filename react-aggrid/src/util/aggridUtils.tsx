import { ITooltipParams, ValueFormatterParams } from 'ag-grid-community'
import moment from 'moment'

export const dateValueFormatter = ({value}: ValueFormatterParams) => value && moment(value).format()

export const headerValueGetter = (params: ITooltipParams) => {
  // fixme hack устанавливает tooltip заголовка как его название
  const headerName = params.colDef.headerName
  if (headerName?.length > 18) {
    params.colDef.headerTooltip = headerName
  }
  return headerName
}
