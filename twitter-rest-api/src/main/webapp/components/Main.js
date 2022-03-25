import { Stack } from "@chakra-ui/react";

export const Main = (props) => (
	<Stack
		spacing="1.5rem"
		width="100%"
		maxWidth="48rem"
		pt="4rem"
		px={["1.5rem", "2rem", "2rem", "2rem"]}
		{...props}
	/>
);
