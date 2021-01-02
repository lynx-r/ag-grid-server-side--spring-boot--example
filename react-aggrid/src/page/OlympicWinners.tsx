import { DataTable } from 'component'
import { DataType } from 'model'
import React from 'react'

interface OlympicWinnersProps {

}

const OlympicWinners = (props: OlympicWinnersProps) => {
  console.log('???')
  return (
    <div>
      <DataTable type={DataType.OLYMPIC_WINNER}/>
    </div>
  )
}

export default OlympicWinners
