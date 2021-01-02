import { EnumPages } from 'model'
import React from 'react'
import { Link } from 'react-router-dom'
import { Flex, Text } from 'rebass'

export const DefaultMenuHeader = () => {
  return (
    <Flex
      px={2}
      color='white'
      bg='black'
      alignItems='center'>
      <Text p={2} fontWeight='bold'>React AgGrid Server Side Model Example</Text>
      <Link to={EnumPages.OLYMPIC_WINNERS}>
        Olympic Winners
      </Link>
    </Flex>
  );
};
