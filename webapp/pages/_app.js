import { ChakraProvider, ColorModeProvider } from "@chakra-ui/react";
import "../styles/globals.css";
import "bootstrap/dist/css/bootstrap.min.css";

function MyApp({ Component, pageProps }) {
	return (
		<ChakraProvider>
			<ColorModeProvider
				options={{
					useSystemColorMode: false,
				}}
			>
				<Component {...pageProps} />
			</ColorModeProvider>
		</ChakraProvider>
	);
}

export default MyApp;
