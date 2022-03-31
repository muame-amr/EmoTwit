import { React } from "react";
import {
	Modal,
	useColorModeValue,
	ModalContent,
	ModalHeader,
	ModalFooter,
	ModalBody,
	ModalCloseButton,
	Button,
	Text,
	Box,
	Heading,
} from "@chakra-ui/react";
import { InfoIcon, SmallCloseIcon } from "@chakra-ui/icons";
import { Cards } from "../components/Cards";

export const Tweets = ({ overlay, tweets, isOpen, onClose }) => {
	return (
		<>
			<Modal
				closeOnOverlayClick={false}
				isOpen={isOpen}
				onClose={onClose}
				size={"xl"}
				scrollBehavior={"inside"}
				isCentered
			>
				{overlay}
				<ModalContent color={useColorModeValue("gray.800", "gray.200")}>
					{/* <ModalHeader>Modal Title</ModalHeader> */}
					<ModalBody
						mt={3}
						css={{
							"&::-webkit-scrollbar": {
								width: "4px",
							},
							"&::-webkit-scrollbar-track": {
								width: "6px",
							},
							"&::-webkit-scrollbar-thumb": {
								background: "rgba(0, 0, 0, 0.2)",
								borderRadius: "24px",
							},
						}}
					>
						{tweets.length > 0 ? (
							tweets.map((tweet, index) => (
								<Cards
									displayname={tweet.displayname}
									username={tweet.username}
									content={tweet.content}
									twitcon={tweet.twitcon}
									score={tweet.score}
									sentiment={tweet.sentiment}
									index={index}
									key={tweet.id}
								/>
							))
						) : (
							<Box textAlign="center" py={10} px={6}>
								<InfoIcon boxSize={"50px"} color={"blue.500"} />
								<Heading as="h2" size="xl" mt={6} mb={2}>
									Nothing to See Here !
								</Heading>
								<Text color={"gray.500"}>
									"To be, or not to be: that is the question: Whether ’tis
									nobler in the mind to suffer The slings and arrows of
									outrageous fortune, Or to take arms against a sea of troubles,
									And by opposing end them. To die: to sleep...”
								</Text>
							</Box>
						)}
					</ModalBody>
					<ModalFooter>
						<Button
							leftIcon={<SmallCloseIcon />}
							colorScheme="blue"
							mr={3}
							onClick={onClose}
							variant={"solid"}
						>
							Close
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</>
	);
};
