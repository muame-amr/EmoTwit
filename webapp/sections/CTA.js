import {
	Link,
	Button,
	Container,
	Stack,
	Heading,
	Flex,
	Text,
	useBreakpointValue,
	Image,
} from "@chakra-ui/react";
import { useRouter } from "next/router";

export const CTA = (props) => {
	const router = useRouter();
	return (
		<Stack minH={"100vh"} direction={{ base: "column", md: "row" }}>
			<Flex p={8} flex={1} align={"center"} justify={"center"}>
				<Stack spacing={6} w={"full"} maxW={"lg"}>
					<Heading fontSize={{ base: "3xl", md: "4xl", lg: "5xl" }}>
						<Text
							as={"span"}
							position={"relative"}
							_after={{
								content: "''",
								width: "full",
								height: useBreakpointValue({ base: "20%", md: "30%" }),
								position: "absolute",
								bottom: 1,
								left: 0,
								bg: "blue.400",
								zIndex: -1,
							}}
						>
							EmoTwit
						</Text>
						<br />{" "}
						<Text color={"blue.400"} as={"span"}>
							Tweets Emotion Inspection
						</Text>{" "}
					</Heading>
					<Text fontSize={{ base: "md", lg: "lg" }} color={"gray.500"}>
						The Emotwit engine analyzes tweets and assigns sentiment scores
						based on morphological analysis, natural language processing and
						machine learning.
					</Text>
					<Stack direction={{ base: "column", md: "row" }} spacing={4}>
						<Button
							rounded={"full"}
							bg={"blue.400"}
							color={"white"}
							_hover={{ bg: "blue.500" }}
							onClick={() => router.push("/sentiment")}
						>
							Get Started
						</Button>
						<Button
							rounded={"full"}
							colorScheme={"gray"}
							isLoading
							loadingText={"Coming Soon"}
							isDisabled
						>
							About Project
						</Button>
					</Stack>
				</Stack>
			</Flex>
			<Flex flex={1}>
				<Image alt={"Front Man"} objectFit={"cover"} src="./frontman.png" />
			</Flex>
		</Stack>
	);
};
