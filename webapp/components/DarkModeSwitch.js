import { useColorMode, Button } from "@chakra-ui/react";
import { BsFillSunFill, BsMoonStarsFill } from "react-icons/bs";

export const DarkModeSwitch = () => {
	const { colorMode, toggleColorMode } = useColorMode();
	const isDark = colorMode === "dark";
	return (
		<Button
			aria-label="Toggle dark mode"
			onClick={toggleColorMode}
			_focus={{ boxShadow: "none" }}
			position="fixed"
			top="1rem"
			right="1rem"
			bg={"transparent"}
			color={isDark ? "white" : "black"}
			w="fit-content"
		>
			{isDark ? <BsFillSunFill /> : <BsMoonStarsFill />}
		</Button>
	);
};
