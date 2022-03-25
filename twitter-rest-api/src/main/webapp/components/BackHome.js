import { Button, useColorModeValue, Link } from "@chakra-ui/react";
import { FaHome } from "react-icons/fa";

export const BackHome = () => {
	return (
		<Link href="/" target="_self" textDecor="none">
			<Button
				leftIcon={<FaHome />}
				// colorScheme={useColorModeValue("twitter", "blue")}
				bgGradient="linear-gradient(to right, #7928CA, #FF0080)"
				_hover={{
					transform: "translateY(1px)",
				}}
				color="white"
				variant="solid"
				position="fixed"
				top="1rem"
				left="1rem"
				size="sm"
			>
				Home
			</Button>
		</Link>
	);
};
