import DefaultFooter from './DefaultFooter'
import React from 'react'
import { Router } from '../router'
import { DefaultMenuHeader } from './default-menu-header/DefaultMenuHeader'

const DefaultLayout = () =>
  <div>
    <DefaultMenuHeader/>
    <div className="pos-relative h100">
      <Router/>
    </div>
    <DefaultFooter/>
  </div>

export default DefaultLayout
