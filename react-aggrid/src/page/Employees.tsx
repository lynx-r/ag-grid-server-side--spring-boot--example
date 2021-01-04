import { DataTable } from 'component'
import { DataType } from 'model'
import React from 'react'

const Employees = () =>
  <div>
    <DataTable type={DataType.EMPLOYEES}/>
  </div>

export default Employees
