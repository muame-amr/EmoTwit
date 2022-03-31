import { React, useState, useEffect } from "react";
import { ModalOverlay, Stack, useDisclosure } from "@chakra-ui/react";
import { Main } from "../sections/Main";
import { Container } from "../sections/Container";
import { Search } from "../components/Search";
import { Tweets } from "../components/Tweets";
import { DarkModeSwitch } from "../components/DarkModeSwitch";
import { BackHome } from "../components/BackHome";
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
		fetch("http://localhost:8080/api/view")
			.then((res) => res.json())
			.then((data) => setTweets(data))
			.catch(console.log);
	}, []);

	const handleSubmit = async () => {
		const api = "http://localhost:8080/api/search?";
		const queryParam = "keyword=" + encodeURIComponent(keyword);
		await fetch(api + queryParam, {
			method: "POST",
		})
			.then((res) => res.json())
			.then((data) => setTweets(data));
		setOverlay(<OverlayOne />);
		onOpen();
	};

	const handleKeyword = (e) => {
		e.preventDefault();
		setKeyword(e.target.value);
		console.log(keyword);
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
