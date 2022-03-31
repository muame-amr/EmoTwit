import { Stack } from "@chakra-ui/react";
import { React, useState } from "react";
import { Cards } from "../components/Cards";

export default function Sentiment() {
	return (
		<>
			<Stack direction={"column"} align={"center"} justify={"center"} mt={10}>
				<Cards />
			</Stack>
		</>
	);
}
