import { DataTable } from 'component'
import { DataType } from 'model'
import React from 'react'

const OlympicWinners = () =>
  <div>
    <DataTable type={DataType.OLYMPIC_WINNER}/>
  </div>

export default OlympicWinners
