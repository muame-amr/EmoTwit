import { Button, useColorModeValue, Link } from "@chakra-ui/react";
import { useRouter } from "next/router";
import { BsFillHouseDoorFill } from "react-icons/bs";

export const BackHome = () => {
	const router = useRouter();
	return (
		<Button
			as={Link}
			bg={"transparent"}
			_focus={{ boxShadow: "none" }}
			position="fixed"
			top="1rem"
			right="5rem"
			w="fit-content"
			onClick={() => router.push("/")}
		>
			<BsFillHouseDoorFill />
		</Button>
	);
};
