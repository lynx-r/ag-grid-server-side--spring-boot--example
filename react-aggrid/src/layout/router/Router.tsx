import { EnumPages } from 'model'
import React, { lazy, Suspense } from 'react'
import { Redirect, Route, Switch } from 'react-router-dom'

const OlympicWinners = lazy(() => import('page/OlympicWinners'))
const Employees = lazy(() => import('page/Employees'))
const Page404 = lazy(() => import('page/Page404'))

const Routes = () =>
  <Switch>
    <Route exact path={EnumPages.HOME}>
      <Redirect to={EnumPages.ROOT_REDIRECT}/>
    </Route>

    <Route exact path={EnumPages.OLYMPIC_WINNERS}>
      <OlympicWinners/>
    </Route>

    <Route exact path={EnumPages.EMPLOYEES}>
      <Employees/>
    </Route>

    <Route>
      <Page404/>
    </Route>
  </Switch>

const Router = () => <>
  <Suspense fallback={<span>Loading...</span>}>
    <Switch>
      <Route>
        <Routes/>
      </Route>
    </Switch>
  </Suspense>
</>

export default Router
