import { React, useState } from "react";
import { Button } from "react-bootstrap";

export default function Sentiment() {
	return (
		<>
			<Button variant="primary" onClick={handleShow}>
				Launch static backdrop modal
			</Button>
		</>
	);
}
