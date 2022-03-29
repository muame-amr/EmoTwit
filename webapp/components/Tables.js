import React from "react";
import {
	Flex,
	Box,
	Table,
	Th,
	Tr,
	Td,
	Thead,
	Tbody,
	Tfoot,
	useColorModeValue,
} from "@chakra-ui/react";
import { InfoIcon } from "@chakra-ui/icons";

export const Tables = ({ tweets }) => {
	return (
		<Box
			overflowY="auto"
			maxHeight="350px"
			sx={{
				"&::-webkit-scrollbar": {
					width: "4px",
					borderRadius: "8px",
					backgroundColor: useColorModeValue(
						`rgba(0, 0, 0, 0.05)`,
						`rgba(255, 255, 255, 0.05)`
					),
				},
				"&::-webkit-scrollbar-thumb": {
					borderRadius: "8px",
					backgroundColor: useColorModeValue(
						`rgba(0, 0, 0, 0.05)`,
						`rgba(255, 255, 255, 0.05)`
					),
				},
			}}
		>
			<Table variant="simple" w="40rem">
				<Thead
					position="sticky"
					top="0"
					bg={useColorModeValue("gray.200", "gray.800")}
				>
					<Tr>
						<Th>Username</Th>
						<Th>Tweets</Th>
						<Th isNumeric>Score</Th>
					</Tr>
				</Thead>
				<Tbody>
					{tweets.length > 0 ? (
						tweets.map((tweet) => (
							<Tr key={tweet.id}>
								<Td>{tweet.username}</Td>
								<Td>{tweet.content}</Td>
								<Td>{tweet.sentiment === "Positive" ? "😄" : "😞"}</Td>
							</Tr>
						))
					) : (
						<Tr>
							<Td colSpan={3} textAlign="center">
								<InfoIcon mr={2} /> No tweets to display
							</Td>
						</Tr>
					)}
				</Tbody>
				<Tfoot>
					<Tr>
						<Th>Username</Th>
						<Th>Tweets</Th>
						<Th isNumeric>Score</Th>
					</Tr>
				</Tfoot>
			</Table>
		</Box>
	);
};
