import { Text, Box, Container, Stack, chakra } from "@chakra-ui/react";
import { SocialButton } from "../components/SocialButton";
import { FaGithub } from "react-icons/fa";
import { SiNextdotjs, SiChakraui } from "react-icons/si";

export const Footer = (props) => {
	return (
		// <Flex as="footer" py="2rem">
		// 	<Text>Next⚡Chakra © 2022</Text>
		// </Flex>
		<Box as="footer" py="2rem" position={"fixed"} bottom={1}>
			<Container as={Stack} maxW={"6xl"} py={3} spacing={4} align={"center"}>
				<Text>Powered by NextJS⚡ChakraUI</Text>
				<Stack direction={"row"} spacing={6}>
					<SocialButton label={"https://github.com/muame-amr/EmoTwit"}>
						<FaGithub />
					</SocialButton>
					<SocialButton label={"Next"} href={"https://nextjs.org/"}>
						<SiNextdotjs />
					</SocialButton>
					<SocialButton label={"ChakraUi"} href={"https://chakra-ui.com/"}>
						<SiChakraui />
					</SocialButton>
				</Stack>
				<Text>
					<chakra.span
						bgGradient="linear(to-l, #7928CA, #FF0080)"
						bgClip={"text"}
					>
						EmoTwit
					</chakra.span>{" "}
					© 2022
				</Text>
			</Container>
		</Box>
	);
};
