import {
	Link as ChakraLink,
	Text,
	Code,
	List,
	ListIcon,
	ListItem,
} from "@chakra-ui/react";
import { LinkIcon } from "@chakra-ui/icons";
import { Hero } from "../sections/Hero";
import { Container } from "../sections/Container";
import { Main } from "../sections/Main";
import { DarkModeSwitch } from "../components/DarkModeSwitch";
import { Footer } from "../sections/Footer";
import { AiFillTwitterCircle } from "react-icons/ai";
import Head from "next/head";

const Index = () => (
	<Container height="100vh">
		<Head>
			<title>Sentiment Analysis</title>
			<link rel="icon" href="/feelings.png" />
		</Head>
		<Hero />
		<Main>
			{/* <Text mt={8}>
				Example repository of <Code>Next.js</Code> + <Code>chakra-ui</Code>.
			</Text> */}

			<List spacing={3} my={0} mt="-24vh">
				<ListItem>
					<ListIcon
						as={AiFillTwitterCircle}
						color="twitter.500"
						boxSize="1.1rem"
					/>
					<ChakraLink
						isExternal
						href="/sentiment"
						target="_self"
						flexGrow={1}
						mr={2}
					>
						Sentiment Analysis <LinkIcon />
					</ChakraLink>
				</ListItem>
				<ListItem>
					<ListIcon
						as={AiFillTwitterCircle}
						color="twitter.500"
						boxSize="1.1rem"
					/>
					<ChakraLink
						isExternal
						href="/similarity"
						target="_self"
						flexGrow={1}
						mr={2}
					>
						Similarity Score <LinkIcon />
					</ChakraLink>
				</ListItem>
			</List>
		</Main>
		<DarkModeSwitch />
		<Footer />
	</Container>
);

export default Index;
