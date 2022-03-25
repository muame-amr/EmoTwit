import { Flex, Text, Box, Container, Stack } from "@chakra-ui/react";
import { SocialButton } from "./SocialButton";
import { FaGithub } from "react-icons/fa";
import { SiNextdotjs, SiChakraui } from "react-icons/si";

export const Footer = (props) => {
	return (
		// <Flex as="footer" py="2rem">
		// 	<Text>Next⚡Chakra © 2022</Text>
		// </Flex>
		<Box as="footer" py="2rem">
			<Container as={Stack} maxW={"6xl"} py={3} spacing={4}>
				<Text>Next⚡Chakra © 2022</Text>
				<Stack direction={"row"} spacing={6}>
					<SocialButton label={"Github"} href={"https://github.com/muame-amr"}>
						<FaGithub />
					</SocialButton>
					<SocialButton label={"Next"} href={"https://nextjs.org/"}>
						<SiNextdotjs />
					</SocialButton>
					<SocialButton label={"ChakraUi"} href={"https://chakra-ui.com/"}>
						<SiChakraui />
					</SocialButton>
				</Stack>
			</Container>
		</Box>
	);
};
