import React from "react";
import {
	Box,
	HStack,
	FormControl,
	InputGroup,
	InputLeftElement,
	Input,
	InputRightElement,
	Icon,
	IconButton,
	useColorModeValue,
} from "@chakra-ui/react";
import { SearchIcon } from "@chakra-ui/icons";
import { FaTwitter } from "react-icons/fa";

export const Search = ({ keyword, handleKeyword, handleSubmit }) => {
	return (
		<Box
			bg={useColorModeValue("azure", "blackAlpha.500")}
			w="32rem"
			p={1}
			boxshadow="sm"
			rounded="lg"
			mx="auto"
		>
			<HStack spacing={3}>
				<FormControl>
					<InputGroup size="lg">
						<InputLeftElement>
							<Icon as={FaTwitter}></Icon>
						</InputLeftElement>
						<Input
							name="keywords"
							type="name"
							value={keyword}
							placeholder='"nasi lemak" "#ramadhan"'
							_placeholder={{
								color: useColorModeValue("gray.800", "gray.200"),
								opacity: 0.5,
								fontStyle: "italic",
							}}
							onChange={handleKeyword}
						/>
						<InputRightElement>
							<IconButton type="submit" onClick={handleSubmit}>
								<SearchIcon />
							</IconButton>
						</InputRightElement>
					</InputGroup>
				</FormControl>
			</HStack>
		</Box>
	);
};
