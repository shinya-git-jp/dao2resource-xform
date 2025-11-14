import { createTheme } from '@mui/material/styles';
import { red } from '@mui/material/colors';

// A custom theme for this app
const theme = createTheme({
  cssVariables: true,
  palette: {
    primary: {
      main: '#1b85d1',
    },
    secondary: {
      main: '#19857b',
    },
    error: {
      main: red.A400,
    },
  },
  typography: {
    fontFamily: [
        "Skranji"
    ].join(",")
  },
  components: {
    MuiStepConnector: {
      styleOverrides: {
        root: {
          borderColor: '#ccc', // Set the color of the stepper connector line
        },
      },
    },
    MuiStepLabel: {
    }
    }
});

export default theme;