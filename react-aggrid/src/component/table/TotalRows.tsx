import React, { Fragment } from 'react'
import { Text } from 'rebass'

interface TotalRowsProps {
  count?: number
}

const TotalRows = ({count}: TotalRowsProps) =>
  !!count
    ? <Text type="text">
      Total rows {count}
    </Text>
    : <Fragment/>

export default TotalRows
