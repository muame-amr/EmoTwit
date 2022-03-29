import { React, useState, useEffect } from "react";
import { Heading, Stack } from "@chakra-ui/react";
import { Main } from "../sections/Main";
import { Container } from "../sections/Container";
import { Search } from "../components/Search";
import { Tables } from "../components/Tables";
import { DarkModeSwitch } from "../components/DarkModeSwitch";
import { BackHome } from "../components/BackHome";
import { Hero } from "../sections/Hero";

export default function Sentiment() {
	const [tweets, setTweets] = useState([]);
	const [keyword, setKeyword] = useState("");

	useEffect(() => {
		fetch("http://localhost:8080/api/view")
			.then((res) => res.json())
			.then((data) => setTweets(data))
			.catch(console.log);
	}, []);

	const handleSubmit = () => {
		const api = "http://localhost:8080/api/search?";
		const queryParam = "keyword=" + encodeURIComponent(keyword);
		fetch(api + queryParam, {
			method: "POST",
		})
			.then((res) => res.json())
			.then((data) => setTweets(data));
	};

	const handleKeyword = (e) => {
		e.preventDefault();
		setKeyword(e.target.value);
		// console.log(keyword);
	};

	return (
		<Container height="100vh">
			<BackHome />
			<DarkModeSwitch />
			<Heading
				pt="1rem"
				textAlign="center"
				fontSize="48px"
				size="lg"
				bgGradient="linear(to-l, #7928CA, #FF0080)"
				bgClip="text"
			>
				Twitter-Sentimental-Analysis
			</Heading>
			<Main>
				<Stack align="center" spacing={12}>
					<Search
						keyword={keyword}
						handleKeyword={handleKeyword}
						handleSubmit={handleSubmit}
					/>
					<Tables tweets={tweets} />
				</Stack>
			</Main>
		</Container>
	);
}
