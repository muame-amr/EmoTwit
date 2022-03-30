import { Container } from "../sections/Container";
import { DarkModeSwitch } from "../components/DarkModeSwitch";
import Head from "next/head";
import { CTA } from "../sections/CTA";

const Index = () => (
	<Container height="100vh">
		<Head>
			<title>EmoTwit</title>
			<link rel="icon" href="/feelings.png" />
		</Head>
		<CTA />
		<DarkModeSwitch />
	</Container>
);

export default Index;
