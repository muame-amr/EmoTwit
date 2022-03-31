import { Button, VisuallyHidden, useColorModeValue } from "@chakra-ui/react";

export const SocialButton = ({ children, label, href }) => {
	return (
		<Button
			bg={useColorModeValue("black", "gray.100")}
			color={useColorModeValue("gray.100", "black")}
			rounded="full"
			w={8}
			h={8}
			p={0}
			cursor={"pointer"}
			as={"a"}
			href={href}
			target={"_blank"}
			display={"inline-flex"}
			alignItems={"center"}
			justifyContent={"center"}
			transition={"background 0.3s ease"}
			_hover={{
				bg: useColorModeValue("blackAlpha.200", "whiteAlpha.600"),
				color: useColorModeValue("blackAlpha.800", "whiteAlpha.800"),
			}}
		>
			<VisuallyHidden>{label}</VisuallyHidden>
			{children}
		</Button>
	);
};
