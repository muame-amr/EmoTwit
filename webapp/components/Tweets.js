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
} from "@chakra-ui/react";
import { InfoIcon } from "@chakra-ui/icons";
import TweetEmbed from "react-tweet-embed";

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
						{tweets.map((tweet) => (
							<TweetEmbed tweetId={tweet.idstring} key={tweet.id} />
						))}
					</ModalBody>
					<ModalFooter>
						<Button colorScheme="blue" mr={3} onClick={onClose}>
							Close
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</>
	);
};
