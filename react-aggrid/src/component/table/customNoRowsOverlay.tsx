import React from 'react'
import { Text } from 'rebass'

const customNoRowsOverlayFactory = (description: string) => () => <Text>{description}</Text>
export default customNoRowsOverlayFactory
