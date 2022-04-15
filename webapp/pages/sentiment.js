import { React, useState, useEffect } from "react";
import { ModalOverlay, Stack, useDisclosure } from "@chakra-ui/react";
import { Main } from "../sections/Main";
import { Container } from "../sections/Container";
import { Search } from "../components/Search";
import { Tweets } from "../components/Tweets";
import { DarkModeSwitch } from "../components/DarkModeSwitch";
import { BackHome } from "../components/BackHome";
import { getTweet, searchTweet } from "./api/sentiment";
import Head from "next/head";
import { Footer } from "../sections/Footer";

export default function Sentiment() {
	const OverlayOne = () => (
		<ModalOverlay
			bg="blackAlpha.300"
			backdropFilter="blur(10px) hue-rotate(90deg)"
		/>
	);

	const [tweets, setTweets] = useState([]);
	const [keyword, setKeyword] = useState("");
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [overlay, setOverlay] = useState(<OverlayOne />);

	useEffect(() => {
		getTweet().then((res) => {
			setTweets(res);
		});
	}, []);

	const handleSubmit = async () => {
		searchTweet(keyword).then((res) => {
			setTweets(res);
		});
		setOverlay(<OverlayOne />);
		onOpen();
	};

	const handleKeyword = (e) => {
		e.preventDefault();
		setKeyword(e.target.value);
		// console.log(keyword);
	};

	return (
		<Container minH="100vh">
			<Head>
				<title>EmoTwit - Sentimen Analysis</title>
				<link rel="icon" href="/feelings.png" />
			</Head>
			<BackHome />
			<DarkModeSwitch />
			<Tweets
				overlay={overlay}
				isOpen={isOpen}
				onClose={onClose}
				tweets={tweets}
			/>
			<Main>
				<Stack align={"center"} justify={"center"}>
					<Search
						keyword={keyword}
						handleKeyword={handleKeyword}
						handleSubmit={handleSubmit}
					/>
				</Stack>
			</Main>
			<Footer />
		</Container>
	);
}
